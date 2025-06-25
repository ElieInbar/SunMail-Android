#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H

#include <functional>
#include <string>
#include <unordered_set>
#include <vector>

#include "IFilter.h"
#include "IHash.h"

class BloomFilter : public IFilter {
   private:
    std::vector<bool> bitArray;
    std::vector<int> hashRepeats;
    std::unordered_set<std::string> blacklist;
    IHash* hashFunction;

   public:
    BloomFilter(std::vector<bool>& bitArray, const std::vector<int>& hashRepeats, std::unordered_set<std::string>& blacklist, IHash* hashFunction = nullptr);
    void add(const std::string& url) override;
    bool check(const std::string& url) override;
    bool isBlacklisted(const std::string& url);
    int repeatedHash(const std::string& input, int times) const;
    std::vector<bool> getBitArray() override;
    std::vector<int> gethashRepeats();
    std::unordered_set<std::string> getBlacklist() override;
    IHash* getHashFunction();
    void remove(const std::string& url) override;
};

#endif  // BLOOMFILTER_H
