import socket
import sys

def main(ip, port):
    try:
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((ip, port))

        while True:
            try:
                command = input()
            except KeyboardInterrupt:
                break
            client_socket.sendall((command+"\n").encode())

            response = bytearray()
            while True:
                data = client_socket.recv(1024)
                if not data:
                    break
                response.extend(data)
                if len(data) < 1024:
                    break
            print(response.decode(), end="")
    except Exception:
        pass 
    finally:
        client_socket.close()


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit(1)

    server_ip = sys.argv[1]
    server_port = int(sys.argv[2])
    main(server_ip, server_port)
