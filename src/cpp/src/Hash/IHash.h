#ifndef IHASH_H
#define IHASH_H

#include <string>
class IHash {
   public:
    // Destructor
    virtual ~IHash() = default;

    // Method to hash a string
    virtual size_t toHash(const std::string& input) const = 0;
};

#endif  // IHASH_H
