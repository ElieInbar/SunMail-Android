#ifndef FILESAVEFILTER_H
#define FILESAVEFILTER_H

#include <fstream>
#include <iostream>
#include <string>
#include <vector>

#include "ISaveFilter.h"

class FileSaveFilter : public ISaveFilter {
   private:
    std::string m_fileName;

   public:
    FileSaveFilter(std::string fileName);
    void save(std::vector<bool> bitArray) override;
    void upload(std::vector<bool>& bitArray) override;
};

#endif