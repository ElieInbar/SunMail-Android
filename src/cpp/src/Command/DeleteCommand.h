#ifndef DELETECOMMAND_H
#define DELETECOMMAND_H

#include <string>
#include "ICommand.h"
#include "BloomFilter.h"

class DeleteCommand : public ICommand {
public:
std::string execute(IFilter& filter, const std::string& url) override;
};
#endif  // DELETECOMMAND_H
