#ifndef GETCOMMAND_H
#define GETCOMMAND_H

#include "BloomFilter.h"
#include "ICommand.h"

class GetCommand : public ICommand {
   public:
   std::string execute(IFilter& filter, const std::string& url) override;
};

#endif  // CHECKURLCOMMAND_H
