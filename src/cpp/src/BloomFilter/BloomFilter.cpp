#include "BloomFilter.h"
#include <iostream>
using namespace std;

BloomFilter::BloomFilter(vector<bool>& bitArray,
                         const vector<int>& hashRepeats, std::unordered_set<std::string>& blacklist,
                         IHash* hashFunction)
    : bitArray(bitArray),
      hashRepeats(hashRepeats),
      hashFunction(hashFunction),
      blacklist(blacklist) {}

void BloomFilter::add(const string& url) {
    for (int times : hashRepeats) {
        int index = repeatedHash(url, times);
        bitArray[index] = true;
    }
    blacklist.insert(url);
}

bool BloomFilter::check(const string& url) {
    for (int times : hashRepeats) {
        int index = repeatedHash(url, times);
        if (!bitArray[index])
            return false;
    }
    return true;
}

bool BloomFilter::isBlacklisted(const string& url) {
    return check(url) && blacklist.count(url);
}

void BloomFilter::remove(const string& url) {
    if (blacklist.count(url)) {
        blacklist.erase(url);
    }
}

int BloomFilter::repeatedHash(const string& input, int times) const {
    string value = input;
    for (int i = 0; i < times; ++i) {
        value = to_string(hashFunction->toHash(value));
    }
    return static_cast<int>(stoull(value) % bitArray.size());
}

vector<bool> BloomFilter::getBitArray() {
    return bitArray;
}

vector<int> BloomFilter::gethashRepeats() {
    return hashRepeats;
}

unordered_set<string> BloomFilter::getBlacklist() {
    return blacklist;
}

IHash* BloomFilter::getHashFunction() {
    return hashFunction;
}
