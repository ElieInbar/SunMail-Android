#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

#include <atomic>
#include <cassert>
#include <cstdio>
#include <fstream>
#include <iostream>
#include <locale>
#include <memory>
#include <mutex>
#include <stdexcept>
#include <vector>

#include "App.h"
#include "CommandParser.h"
#include "DeleteCommand.h"
#include "GetCommand.h"
#include "ICommand.h"
#include "IExecutor.h"
#include "IInput.h"
#include "PostCommand.h"
#include "ThreadExecutor.h"
#include "oneExecutor.h"

class Server {
   public:
    static std::atomic<int> activeThreads;
    static std::mutex mutex;

    void startServer(const int server_port, IFilter* bf);

    void handleClient(int client_sock, IFilter* bf);

    bool isNumber(std::string str);

    bool checkValidArgs(std::vector<int> hashFunc);
};