#include "App.h"

App::App(CommandParser parser) : m_parser(parser) {}

void App::addCommand(const std::string& key, ICommand* command) {
    m_commands[key] = command;
}

IFilter* App::load(int size, const std::vector<int>& hashRepeats, ISaveFilter*& fileSaveFilter, ISaveURL*& fileSaveURL) {
    fileSaveURL = new FileSaveURL("BlacklistSave");
    std::unordered_set<std::string> blacklist;
    fileSaveURL->upload(blacklist);

    fileSaveFilter = new FileSaveFilter("BloomFilterSave");
    std::vector<bool> bitArray(size, false);
    fileSaveFilter->upload(bitArray);
    IHash* hashFunction = new StdHash();

    return new BloomFilter(bitArray, hashRepeats, blacklist, hashFunction);
}

std::map<std::string, ICommand*> App::getMCommand() {
    return m_commands;
}