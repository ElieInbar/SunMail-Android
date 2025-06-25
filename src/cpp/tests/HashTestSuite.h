#ifndef HASH_TEST_SUITE_H
#define HASH_TEST_SUITE_H

#include "IHash.h"
#include "gtest/gtest.h"
#include <memory>
#include <string>

// This function runs common hash tests on the provided hasher
// It takes a label for the test and a unique pointer to an IHash object
// The label is used for debugging purposes to identify which test is running
inline void runCommonHashTests(const std::string& label, std::unique_ptr<IHash> hasher) {
    // For the gtest
    // This is a scoped trace to help identify which test is running
    SCOPED_TRACE("[" + label + "]");


    // Test 1 : same input should give same hash
    std::string input = "example.com";
    EXPECT_EQ(hasher->toHash(input), hasher->toHash(input));


    // Test 2 : empty string does not crash
    EXPECT_NO_THROW(hasher->toHash(""));


    // Test 3 : different inputs should give different hashes
    std::string a = "url1.com";
    std::string b = "url2.com";
    EXPECT_NE(hasher->toHash(a), hasher->toHash(b));
}

#endif
