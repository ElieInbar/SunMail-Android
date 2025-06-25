#include "CommandParser.h"

#include <algorithm>
#include <iostream>
#include <sstream>

CommandParser::CommandParser() : m_valid(false) {}

void CommandParser::parse(const std::string& rawInput) {
    std::istringstream iss(rawInput);
    std::string cmd;
    std::string url;

    iss >> cmd >> url;
    if (cmd.empty() || url.empty() || !checkURL(url)) {
        m_valid = false;
        return;
    }

    if (cmd == "GET" || cmd == "POST" || cmd == "DELETE") {
        m_command = cmd;
        m_url = url;
        m_valid = true;
    } else {
        m_valid = false;
    }
}

bool CommandParser::isValid() const {
    return m_valid;
}

std::string CommandParser::getCommand() const {
    return m_command;
}

std::string CommandParser::getURL() const {
    return m_url;
}

bool CommandParser::checkURL(std::string url) {
    std::regex pattern(R"(^((https:\/\/|http:\/\/)?(www\.)?)(?!w{1,2}\.)(?!w{4,}\.)[a-zA-Z0-9]{1,}(\.[a-zA-Z0-9]{2,})(\.[a-zA-Z0-9]{2,})?$)");

    return std::regex_match(url, pattern);
}