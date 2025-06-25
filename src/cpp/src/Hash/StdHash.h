#ifndef STDHASH_H
#define STDHASH_H

#include <string>

#include "IHash.h"

class StdHash : public IHash {
   public:
    // Destructor
    virtual ~StdHash() = default;

    // Method to hash a string
    size_t toHash(const std::string& input) const;
};
#endif