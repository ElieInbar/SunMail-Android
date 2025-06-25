#ifndef APP_H
#define APP_H

#include <map>

#include "BloomFilter.h"
#include "CommandParser.h"
#include "FileSaveFilter.h"
#include "FileSaveURL.h"
#include "ICommand.h"
#include "IFilter.h"
#include "IHash.h"
#include "IInput.h"
#include "ISaveFilter.h"
#include "ISaveURL.h"
#include "StdHash.h"

class App {
   private:
    CommandParser m_parser;
   std::map<std::string, ICommand*> m_commands;

   public:
    App(CommandParser parser);
    void addCommand(const std::string& key, ICommand* command);
    IFilter* load(int size, const std::vector<int>& hashRepeats, ISaveFilter*& fileSaveFilter, ISaveURL*& fileSaveURL);
    std::map<std::string, ICommand*> getMCommand();
};

#endif