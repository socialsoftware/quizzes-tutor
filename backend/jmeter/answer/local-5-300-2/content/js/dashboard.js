/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 6;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "KO",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "OK",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.05061996676466828, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, "Submit answer 16"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 15"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 14"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 13"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 12"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 11"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 10"], "isController": false}, {"data": [0.0, 500, 1500, "Get quizzes available"], "isController": false}, {"data": [0.24333333333333335, 500, 1500, "Login as student"], "isController": false}, {"data": [0.0, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 2"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 3"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 4"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 5"], "isController": false}, {"data": [1.0, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 6"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 7"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 8"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 9"], "isController": false}, {"data": [0.0, 500, 1500, "Submit null answer 4"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 1"], "isController": false}, {"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.0, 500, 1500, "Start quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 20"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 19"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 18"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 17"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 7823, 0, 0.0, 7921.98223188036, 0, 16119, 10886.0, 11079.0, 12473.76, 36.83457166803213, 30.246736091982843, 28.05332600867776], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Submit answer 16", 300, 0, 0.0, 10899.01666666668, 7805, 13778, 11763.9, 12750.6, 13356.650000000001, 8.895478131949591, 3.4053002223869533, 7.3578808376575235], "isController": false}, {"data": ["Submit answer 15", 300, 0, 0.0, 10750.026666666681, 8825, 12989, 11135.2, 12221.75, 12745.69, 8.997660608241857, 3.444416951592586, 7.442400913262552], "isController": false}, {"data": ["Submit answer 14", 300, 0, 0.0, 10705.193333333336, 6731, 14226, 11212.100000000002, 12624.6, 13051.49, 9.42003956416617, 3.6061088956573615, 7.7917710066882275], "isController": false}, {"data": ["Submit answer 13", 300, 0, 0.0, 8114.793333333334, 6000, 11688, 9309.9, 10158.6, 11035.630000000003, 11.20197154699227, 4.2882547328329785, 9.265693262014114], "isController": false}, {"data": ["Submit answer 12", 300, 0, 0.0, 7498.923333333335, 6126, 9135, 7806.0, 7858.9, 8526.94, 12.808470668602169, 4.9032426778242675, 10.594506500298865], "isController": false}, {"data": ["Submit answer 11", 300, 0, 0.0, 7417.023333333338, 5936, 9125, 7865.1, 7960.55, 8324.850000000002, 12.960082944530845, 4.961281752203214, 10.70725602643857], "isController": false}, {"data": ["Submit answer 10", 300, 0, 0.0, 7557.893333333332, 6338, 10095, 8011.6, 8045.8, 9213.94, 12.908777969018933, 4.94164156626506, 10.664869298623064], "isController": false}, {"data": ["Get quizzes available", 300, 0, 0.0, 5543.66, 1910, 7394, 6204.000000000001, 6315.0, 7171.450000000001, 25.113008538422903, 73.67136489201407, 19.349769274234053], "isController": false}, {"data": ["Login as student", 300, 0, 0.0, 1921.609999999999, 76, 4272, 3417.8, 3620.75, 3785.98, 59.11330049261083, 74.06480911330048, 7.677801724137931], "isController": false}, {"data": ["Conclude quiz", 300, 0, 0.0, 9446.376666666656, 2616, 12564, 10560.500000000002, 10713.2, 11928.79, 12.2684333210649, 5.331496902220587, 9.333114801864802], "isController": false}, {"data": ["Submit answer 2", 300, 0, 0.0, 7927.216666666666, 6148, 10293, 8705.300000000001, 8807.9, 10034.500000000005, 14.875049583498612, 5.694354918683063, 12.274821189508133], "isController": false}, {"data": ["Submit answer 3", 300, 0, 0.0, 7785.193333333337, 5918, 11002, 8562.000000000002, 8751.7, 9530.6, 14.107688690336232, 5.4005995767693395, 11.65537561721138], "isController": false}, {"data": ["Submit answer 4", 300, 0, 0.0, 8351.82, 5010, 10475, 9013.7, 9200.9, 10053.240000000002, 14.000373343289153, 5.359517920477879, 11.566714695725219], "isController": false}, {"data": ["Submit answer 5", 300, 0, 0.0, 7421.353333333338, 5707, 9263, 7907.6, 7996.9, 8775.32, 13.368983957219251, 5.117814171122994, 11.045078542780749], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 62.0, 62, 62, 62.0, 62.0, 62.0, 16.129032258064516, 20.208543346774192, 2.094884072580645], "isController": false}, {"data": ["Submit answer 6", 300, 0, 0.0, 7535.779999999994, 6091, 9139, 7914.9, 8002.95, 8823.180000000002, 13.013490651975882, 4.981726890209517, 10.751379972237887], "isController": false}, {"data": ["Submit answer 7", 300, 0, 0.0, 7716.686666666663, 5951, 9345, 8191.6, 8275.65, 9197.73, 12.410540685889215, 4.750910106316965, 10.25323966822488], "isController": false}, {"data": ["Submit answer 8", 300, 0, 0.0, 7883.096666666666, 5058, 9730, 8432.0, 8492.9, 9635.790000000006, 12.52975817566721, 4.796548051622604, 10.351733805287557], "isController": false}, {"data": ["Submit answer 9", 300, 0, 0.0, 7435.773333333334, 5853, 9157, 7972.6, 8013.95, 8747.670000000002, 12.523481527864746, 4.794145272385723, 10.334318252974327], "isController": false}, {"data": ["Submit null answer 4", 300, 0, 0.0, 7538.866666666663, 6177, 9324, 8063.9, 8164.9, 8797.28, 13.099292638197536, 5.014572963059995, 10.796682604139376], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 74.0, 74, 74, 74.0, 74.0, 74.0, 13.513513513513514, 164.26045185810813, 20.877322635135137], "isController": false}, {"data": ["Submit answer 1", 300, 0, 0.0, 7535.986666666668, 5416, 8860, 8433.0, 8598.95, 8806.78, 15.64781973711663, 5.990180993114959, 12.927788571875652], "isController": false}, {"data": ["Get list of questions", 1, 0, 0.0, 131.0, 131, 131, 131.0, 131.0, 131.0, 7.633587786259541, 1007.5292223282443, 5.792282919847328], "isController": false}, {"data": ["Start quiz", 300, 0, 0.0, 6865.133333333336, 5264, 9546, 8268.9, 8452.85, 8765.380000000003, 17.167381974248926, 109.59825420243203, 13.009656652360514], "isController": false}, {"data": ["Submit answer 20", 300, 0, 0.0, 10544.096666666665, 8453, 14087, 10816.0, 10896.85, 12472.68, 9.310409037303705, 3.564140959592825, 7.701090287691639], "isController": false}, {"data": ["Debug Sampler", 300, 0, 0.0, 0.14333333333333342, 0, 3, 1.0, 1.0, 1.0, 21.396476713501176, 38.896315125525994, 0.0], "isController": false}, {"data": ["Create question", 20, 0, 0.0, 86.15, 69, 243, 135.80000000000013, 237.89999999999992, 243.0, 11.534025374855824, 11.433778474625145, 13.585144535755479], "isController": false}, {"data": ["Submit answer 19", 300, 0, 0.0, 10428.133333333333, 7125, 14050, 10733.0, 11926.0, 12350.96, 9.087604507451834, 3.478848600508906, 7.5167978689567425], "isController": false}, {"data": ["Submit answer 18", 300, 0, 0.0, 10819.313333333337, 7266, 12914, 11193.9, 12313.8, 12658.710000000001, 8.980153859969468, 3.437715149519562, 7.42792023378334], "isController": false}, {"data": ["Submit answer 17", 300, 0, 0.0, 10929.146666666666, 7631, 16119, 11695.9, 12291.9, 13319.580000000004, 8.84433962264151, 3.3857237617924527, 7.315581699587264], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Percentile 1
            case 8:
            // Percentile 2
            case 9:
            // Percentile 3
            case 10:
            // Throughput
            case 11:
            // Kbytes/s
            case 12:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 7823, 0, null, null, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
