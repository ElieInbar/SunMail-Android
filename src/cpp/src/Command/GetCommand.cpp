#include "GetCommand.h"

#include <iostream>

std::string GetCommand::execute(IFilter& filter, const std::string& url) {
    std::string output = "200 Ok\n\n";
    if (!filter.check(url)) {
        output += "false\n";
    } else {
        output += "true ";
        if (filter.isBlacklisted(url)) {
            output += "true\n";
        } else {
            output += "false\n";
        }
    }
    return output;
}