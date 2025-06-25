#ifndef THREADEXXECUTOR_H
#define THREADEXXECUTOR_H

#include <functional>
#include <iostream>
#include <thread>

#include "IExecutor.h"

class ThreadExecutor : public IExecutor {
    void submit(std::function<void()> task);
    ~ThreadExecutor() = default;
};

#endif