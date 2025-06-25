#include <string>
using namespace std;
#include "StdHash.h"

// Include the standard library for hashing
size_t StdHash::toHash(const string& input) const {
    return hash<string>()(input);
}
