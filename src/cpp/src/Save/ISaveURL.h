#ifndef ISAVEURL_H
#define ISAVEURL_H

#include <unordered_set>

class ISaveURL {
   public:
    virtual void save(std::unordered_set<std::string> blacklist) = 0;
    virtual void upload(std::unordered_set<std::string>& blacklist) = 0;
};

#endif