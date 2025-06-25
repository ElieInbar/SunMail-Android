#include "StdHash.h"
#include "HashTestSuite.h"
#include "gtest/gtest.h"

TEST(StdHashTest, RunCommonTests) {
    runCommonHashTests("StdHash", std::make_unique<StdHash>());
}
