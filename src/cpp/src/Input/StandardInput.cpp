#include "StandardInput.h"

#include <cctype>
#include <iostream>
#include <regex>
#include <sstream>
#include <vector>

int StandardInput::getIndex() {
    return m_index;
}
std::string StandardInput::getCommand() {
    return std::to_string(m_index);
}

std::string StandardInput::getURL() const {
    return m_URL;
}

bool StandardInput::getValidInput() {
    // get the line from the input
    std::string line;
    getline(std::cin, line);
    std::stringstream stream(line);
    std::string items;
    // counter to see how many words got in the input
    int counter = 0;
    try {
        while (getline(stream, items, ' ')) {
            counter++;
            if (isNumber(items)) {
                if (checkCommand(stoi(items, 0, 10))) {
                    setIndex(stoi(items, 0, 10));
                    continue;
                } else {
                    return false;
                }
            } else if (checkURL(items)) {
                setURL(items);
                continue;
            } else {
                return false;
            }
        }
    } catch (...) {
        return false;
    }
    if (counter != 2) {
        return false;
    }
    return true;
}

bool StandardInput::isNumber(std::string str) {
    char* p;
    strtol(str.c_str(), &p, 10);
    return *p == 0;
}

void StandardInput::setIndex(int index1) {
    m_index = index1;
}

void StandardInput::setURL(std::string url) {
    m_URL = url;
}
bool StandardInput::getFirstInput() {
    sizeAndFunctions.clear();
    // get the line from the input
    std::string line;
    getline(std::cin, line);
    std::stringstream stream(line);
    std::string items;

    try {
        while (getline(stream, items, ' ')) {
            if (isNumber(items)) {
                sizeAndFunctions.push_back(stoi(items, 0, 10));
            } else {
                return false;
            }
        }
    } catch (...) {
        return false;
    }

    return checkValidFirstInput();
}

int StandardInput::getSize() {
    return sizeAndFunctions[0];
}

std::vector<int> StandardInput::getFunctions() {
    std::vector<int> func;
    for (int i = 1; i < sizeAndFunctions.size(); i++) {
        func.push_back(sizeAndFunctions[i]);
    }
    return func;
}

bool StandardInput::checkValidFirstInput() {
    if (sizeAndFunctions.size() < 2) {
        return false;
    } else {
        for (int i = 0; i < sizeAndFunctions.size(); i++) {
            if (sizeAndFunctions[i] <= 0) {
                return false;
            }
        }
    }
    return true;
}

bool StandardInput::checkURL(std::string url) {
    std::regex pattern(
        R"(^((https:\/\/|http:\/\/)?(www\.)?)(?!w{1,2}\.)(?!w{4,}\.)[a-zA-Z0-9]{1,}(\.[a-zA-Z0-9]{2,})(\.[a-zA-Z0-9]{2,})?$)");

    return std::regex_match(url, pattern);
}

bool StandardInput::checkCommand(int index) {
    return index == 1 || index == 2;
}
