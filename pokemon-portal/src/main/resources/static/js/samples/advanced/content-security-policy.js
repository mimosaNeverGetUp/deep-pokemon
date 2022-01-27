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

var utils = Samples.utils;

// CSP: disable automatic style injection
Chart.platform.disableCSSInjection = true;

utils.srand(110);

function generateData() {
	var DATA_COUNT = 16;
	var MIN_XY = -150;
	var MAX_XY = 100;
	var data = [];
	var i;

	for (i = 0; i < DATA_COUNT; ++i) {
		data.push({
			x: utils.rand(MIN_XY, MAX_XY),
			y: utils.rand(MIN_XY, MAX_XY),
			v: utils.rand(0, 1000)
		});
	}

	return data;
}

window.addEventListener('load', function() {
	new Chart('chart-0', {
		type: 'bubble',
		data: {
			datasets: [{
				backgroundColor: utils.color(0),
				data: generateData()
			}, {
				backgroundColor: utils.color(1),
				data: generateData()
			}]
		},
		options: {
			aspectRatio: 1,
			legend: false,
			tooltip: false,
			elements: {
				point: {
					radius: function(context) {
						var value = context.dataset.data[context.dataIndex];
						var size = context.chart.width;
						var base = Math.abs(value.v) / 1000;
						return (size / 24) * base;
					}
				}
			}
		}
	});
});
