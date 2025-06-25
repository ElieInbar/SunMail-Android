#pragma once
#include <string>
#include <regex>


class CommandParser {
public:
    CommandParser();
    bool isValid() const;
    std::string getCommand() const;
    std::string getURL() const;
    void parse(const std::string& rawInput);

private:
    std::string m_command;
    std::string m_url;
    bool m_valid;
    bool checkURL(std::string url);
};