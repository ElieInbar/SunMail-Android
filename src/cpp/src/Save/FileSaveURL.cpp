#include "FileSaveURL.h"

#include <filesystem>
#include <fstream>
#include <iostream>
#include <string>
#include <unordered_set>

FileSaveURL::FileSaveURL(std::string fileName)
    : m_fileName("data/" + fileName) {
    std::filesystem::create_directory("data");
}

void FileSaveURL::save(std::unordered_set<std::string> blacklist) {
    std::ofstream saveFile(m_fileName);
    for (std::string url : blacklist) {
        saveFile << url << std::endl;
    }
    saveFile.close();
}

void FileSaveURL::upload(std::unordered_set<std::string>& blacklist) {
    std::ifstream saveFile(m_fileName);

    std::string line;
    while (getline(saveFile, line)) {
        blacklist.insert(line);
    }
    saveFile.close();
}