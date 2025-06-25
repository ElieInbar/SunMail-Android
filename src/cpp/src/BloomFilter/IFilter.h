#ifndef IFILTER_H
#define IFILTER_H

#include <string>

class IFilter {
   public:
    virtual void add(const std::string& input) = 0;
    virtual bool check(const std::string& input) = 0;
    virtual bool isBlacklisted(const std::string& url) = 0;
    virtual ~IFilter() = default;
    virtual void remove(const std::string& url) = 0;
    virtual std::vector<bool> getBitArray() = 0;
    virtual std::unordered_set<std::string> getBlacklist() = 0;
};

#endif  // IFILTER_H
