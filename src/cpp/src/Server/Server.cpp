#include "Server.h"
CommandParser parser;
App app(parser);
ISaveURL* fileSaveURL = new FileSaveURL("BlacklistSave");
ISaveFilter* fileSaveFilter = new FileSaveFilter("BloomFilterSave");

std::atomic<int> Server::activeThreads{0};
std::mutex Server::mutex;
void Server::handleClient(int client_sock, IFilter* bf) {
    while (true) {
        char buffer[4096];
        memset(buffer, 0, sizeof(buffer));
        int read_bytes = recv(client_sock, buffer, sizeof(buffer) - 1, 0);
        if (read_bytes <= 0) {
            close(client_sock);
            return;
        }
        parser.parse(buffer);
        std::string output;
        if (!parser.isValid()) {
            output = "400 Bad Request\n";
        } else {
            std::string command = parser.getCommand();
            {
                std::lock_guard<std::mutex> lock(Server::mutex);
                // Returning the output of the command:
                output = app.getMCommand()[command]->execute(*bf, parser.getURL());
                fileSaveFilter->save(bf->getBitArray());
                fileSaveURL->save(bf->getBlacklist());
            }
        }
        int sent_bytes = send(client_sock, output.c_str(), output.size(), 0);

        if (sent_bytes < 0) {
            // perror("error sending to client");
            return;
        }
    }
}

void Server::startServer(const int server_port, IFilter* bf) {
    IExecutor* executor = new ThreadExecutor();
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        // perror("Socket creation failed");
        return;
    }

    struct sockaddr_in sin;
    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;
    sin.sin_port = htons(server_port);

    if (bind(sock, (struct sockaddr*) &sin, sizeof(sin)) < 0) {
        // perror("error binding socket");
        return;
    }

    if (listen(sock, 1024) < 0) {
        // perror("error listening to a socket");
        return;
    }

    struct sockaddr_in client_sin;
    unsigned int addr_len = sizeof(client_sin);
    int client_sock;
    while (true) {
        client_sock = accept(sock, (struct sockaddr*) &client_sin, &addr_len);
        if (client_sock < 0) {
            // perror("error accepting client");
            return;
        }
        Server::activeThreads++;
        executor->submit([this, client_sock, bf]() {
            handleClient(client_sock, bf);
            Server::activeThreads--;
        });
    }
}

bool Server::isNumber(std::string str) {
    char* p;
    strtol(str.c_str(), &p, 10);
    return *p == 0;
}

bool Server::checkValidArgs(std::vector<int> hashFunc) {
    if (hashFunc.size() < 2) {
        return false;
    } else {
        for (int i = 0; i < hashFunc.size(); i++) {
            if (hashFunc[i] <= 0) {
                return false;
            }
        }
    }
    return true;
}

int main(int argc, char* argv[]) {
    try {
        Server s;
        int port, filterSize;
        if (s.isNumber(argv[1]) && s.isNumber(argv[2])) {
            port = std::stoi(argv[1]);
            filterSize = std::stoi(argv[2]);
        } else {
            return 1;
        }
        std::vector<int> hashFunc;
        for (int i = 3; i < argc; i++) {
            std::stringstream stream(argv[i]);
            std::string items;
            while (getline(stream, items, ' ')) {
                if (s.isNumber(items)) {
                    hashFunc.push_back(std::stoi(items));
                } else {
                    return 1;
                }
            }
        }

        if (!s.checkValidArgs(hashFunc) || filterSize <= 0 || (port < 0 || port > 65535)) {
            return 1;
        }

        IFilter* bf = app.load(filterSize, hashFunc, fileSaveFilter, fileSaveURL);
        app.addCommand("POST", new PostCommand());
        app.addCommand("GET", new GetCommand());
        app.addCommand("DELETE", new DeleteCommand());

        s.startServer(port, bf);

    } catch (std::invalid_argument& exception) {
        // std::cerr << "Invalid argument getted: " << exception.what() << std::endl;
        return 1;
    } catch (...) {
        // std::cerr << "Unknown error occurred while parsing port." << std::endl;
        return 1;
    }
    return 0;
}