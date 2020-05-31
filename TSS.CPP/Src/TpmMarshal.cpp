/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

//#include <new>

_TPMCPP_BEGIN


void TpmBuffer::writeNum(uint64_t val, size_t len)
{
    // TODO: Enable assert below
    // assert(len > 1);
    if (!this->checkLen(len))
        return;

    if (len == 8) {
        this->buf[this->pos++] = (val >> 56) & 0xFF;
        this->buf[this->pos++] = (val >> 48) & 0xFF;
        this->buf[this->pos++] = (val >> 40) & 0xFF;
        this->buf[this->pos++] = (val >> 32) & 0xFF;
    }
    if (len >= 4) {
        this->buf[this->pos++] = (val >> 24) & 0xFF;
        this->buf[this->pos++] = (val >> 16) & 0xFF;
    }
    if (len >= 2)
        this->buf[this->pos++] = (val >> 8) & 0xFF;
    this->buf[this->pos++] = val & 0xFF;
}

uint64_t TpmBuffer::readNum(size_t len)
{
    if (!this->checkLen(len))
        return 0;

    uint64_t res = 0;
    if (len == 8) {
        res += ((uint64_t)this->buf[this->pos++] << 56);
        res += ((uint64_t)this->buf[this->pos++] << 48);
        res += ((uint64_t)this->buf[this->pos++] << 40);
        res += ((uint64_t)this->buf[this->pos++] << 32);
    }
    if (len >= 4) {
        res += ((uint32_t)this->buf[this->pos++] << 24);
        res += ((uint32_t)this->buf[this->pos++] << 16);
    }
    if (len >= 2)
        res += ((uint16_t)this->buf[this->pos++] << 8);
    res += (uint8_t)this->buf[this->pos++];
    return res;
}

_TPMCPP_END