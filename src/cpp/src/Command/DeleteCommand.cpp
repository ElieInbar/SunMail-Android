#include "DeleteCommand.h"
#include <iostream>

std::string DeleteCommand::execute(IFilter& filter, const std::string& url) {
    if (filter.isBlacklisted(url)) {
        filter.remove(url);
        return "204 No Content\n";
    } else {
        return "404 Not Found\n";
    }
}