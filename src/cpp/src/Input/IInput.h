#ifndef IINPUT_H
#define IINPUT_H
#include <string>
#include <vector>

class IInput {
   public:
    virtual bool getValidInput() = 0;
    virtual int getIndex() = 0;
    virtual std::string getURL() const = 0;
    virtual bool getFirstInput() = 0;
    virtual int getSize() = 0;
    virtual std::vector<int> getFunctions() = 0;
    virtual std::string getCommand() = 0;
};

#endif
