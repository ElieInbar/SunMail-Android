#include "PostCommand.h"
#include <iostream>

std::string PostCommand::execute(IFilter& filter, const std::string& url) {
    filter.add(url);
    return "201 Created\n";
}