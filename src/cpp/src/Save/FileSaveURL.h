#ifndef FILESAVEURL_H
#define FILESAVEURL_H

#include <fstream>
#include <iostream>
#include <string>
#include <unordered_set>

#include "ISaveURL.h"

class FileSaveURL : public ISaveURL {
   private:
    std::string m_fileName;

   public:
    FileSaveURL(std::string fileName);
    void save(std::unordered_set<std::string> blacklist);
    void upload(std::unordered_set<std::string>& blacklist);
};

#endif