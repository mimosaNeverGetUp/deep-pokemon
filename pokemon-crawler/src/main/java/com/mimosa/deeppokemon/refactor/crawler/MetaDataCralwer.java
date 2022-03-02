/*
 * The MIT License
 *
 * Copyright (c) [2022] [Xiaocong Huang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mimosa.deeppokemon.refactor.crawler;

import com.mimosa.deeppokemon.refactor.entity.CrawResource;
import com.mimosa.deeppokemon.refactor.entity.metadata.MetaData;

/**
 * 宝可梦元数据爬取接口
 * 从比赛回放、排行榜等原始爬取源中提取出宝可梦元数据
 *
 * @author huangxiaocong(2070132549@qq.com)
 */
public interface MetaDataCralwer {

    /**
     * 提取宝可梦元数据
     *
     * @param resource 爬取源
     * @return Meata 从爬取源中解析得到的元数据
     * @author huangxiaocong(2070132549@qq.com)
     */
    public MetaData craw(CrawResource resource);
}
