#ifndef IEXECUTOR_H
#define IEXECUTOR_H

#include <iostream>
#include <functional>
class IExecutor {
public:
    virtual void submit(std::function<void()> task) = 0;
    virtual ~IExecutor() = default;
};

#endif 