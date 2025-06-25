#ifndef ISAVEFILTER_H
#define ISAVEFILTER_H

#include <vector>

class ISaveFilter {
   public:
    virtual void save(std::vector<bool> bitArray) = 0;
    virtual void upload(std::vector<bool>& bitArray) = 0;
};

#endif