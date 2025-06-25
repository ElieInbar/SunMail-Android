#ifndef ICOMMAND_H
#define ICOMMAND_H
#include <string>

#include "BloomFilter.h"
#include "IFilter.h"
class ICommand {
   public:
    virtual ~ICommand() = default;

    virtual std::string execute(IFilter& filter, const std::string& url) = 0;
};

#endif  // ICOMMAND_H
