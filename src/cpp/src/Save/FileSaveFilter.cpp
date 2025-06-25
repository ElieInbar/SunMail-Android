#include "FileSaveFilter.h"

#include <filesystem>
#include <fstream>
#include <iostream>
#include <string>
#include <vector>

FileSaveFilter::FileSaveFilter(std::string fileName)
    : m_fileName("data/" + fileName) {
    std::filesystem::create_directory("data");
}

void FileSaveFilter::save(std::vector<bool> bitArray) {
    std::ofstream saveFile(m_fileName);
    for (bool c : bitArray) {
        if (c) {
            saveFile << 1;
        } else {
            saveFile << 0;
        }
    }
    saveFile.close();
}

void FileSaveFilter::upload(std::vector<bool>& bitArray) {
    std::ifstream saveFile(m_fileName);
    char c;
    int i = 0;
    while (saveFile.get(c)) {
        if (c == '0') {
            bitArray.at(i) = false;
        } else if (c == '1') {
            bitArray.at(i) = true;
        }
        i++;
    }
    saveFile.close();
}