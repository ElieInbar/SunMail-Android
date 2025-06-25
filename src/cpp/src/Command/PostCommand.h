#ifndef POSTCOMMAND_H
#define POSTCOMMAND_H

#include <string>

#include "ICommand.h"
#include "IFilter.h"
class PostCommand : public ICommand {
   public:
   std::string execute(IFilter& filter, const std::string& url) override;
};

#endif  // POSTCOMMAND_H
