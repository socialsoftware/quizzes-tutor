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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.06455324044484213, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, "Submit answer 16"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 15"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 14"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 13"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 12"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 11"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 10"], "isController": false}, {"data": [0.025, 500, 1500, "Get quizzes available"], "isController": false}, {"data": [0.5516666666666666, 500, 1500, "Login as student"], "isController": false}, {"data": [0.03, 500, 1500, "Conclude quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 2"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 3"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 4"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 5"], "isController": false}, {"data": [1.0, 500, 1500, "Login as teacher"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 6"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 7"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 8"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 9"], "isController": false}, {"data": [0.0, 500, 1500, "Submit null answer 4"], "isController": false}, {"data": [1.0, 500, 1500, "Create quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 1"], "isController": false}, {"data": [1.0, 500, 1500, "Get list of questions"], "isController": false}, {"data": [0.0, 500, 1500, "Start quiz"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 20"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [1.0, 500, 1500, "Create question"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 19"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 18"], "isController": false}, {"data": [0.0, 500, 1500, "Submit answer 17"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 7823, 0, 0.0, 3636.4026588265365, 0, 7024, 4471.800000000001, 4733.799999999999, 5163.76, 79.65259535300466, 63.84941301647423, 60.663667953397685], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["Submit answer 16", 300, 0, 0.0, 3890.2333333333336, 3148, 4647, 4046.9, 4351.999999999997, 4633.47, 19.363583553863034, 7.412621829213193, 16.016557880978507], "isController": false}, {"data": ["Submit answer 15", 300, 0, 0.0, 3881.9566666666665, 2625, 5154, 4008.8, 4498.0, 5099.920000000005, 19.522353094292964, 7.4734007939090255, 16.147883858267715], "isController": false}, {"data": ["Submit answer 14", 300, 0, 0.0, 3862.2333333333318, 3142, 4627, 3981.9, 4400.95, 4558.89, 19.707022268935162, 7.544094462326742, 16.300632677527425], "isController": false}, {"data": ["Submit answer 13", 300, 0, 0.0, 3804.3133333333335, 3137, 5053, 3879.9, 3922.75, 4581.9000000000015, 19.65280052407468, 7.5233377006223385, 16.25578324598755], "isController": false}, {"data": ["Submit answer 12", 300, 0, 0.0, 3821.8433333333314, 3099, 5087, 3981.7000000000003, 4082.45, 4485.64, 19.845207382417147, 7.5969934510815635, 16.395552192895416], "isController": false}, {"data": ["Submit answer 11", 300, 0, 0.0, 3851.0299999999997, 2983, 5004, 4062.9, 4122.9, 4689.680000000001, 19.296327265710428, 7.386875281404772, 15.960926947321026], "isController": false}, {"data": ["Submit answer 10", 300, 0, 0.0, 3819.3466666666664, 2965, 5025, 4058.8, 4333.65, 4630.85, 19.13265306122449, 7.32421875, 15.806859853316327], "isController": false}, {"data": ["Get quizzes available", 300, 0, 0.0, 2580.1233333333316, 699, 3651, 3093.2000000000003, 3229.7, 3636.8900000000003, 46.51162790697674, 119.68568313953487, 35.8375726744186], "isController": false}, {"data": ["Login as student", 300, 0, 0.0, 911.3666666666668, 63, 2078, 1466.8000000000002, 1604.3999999999999, 1932.8100000000002, 87.92497069167644, 110.07794182297773, 11.419942482415006], "isController": false}, {"data": ["Conclude quiz", 300, 0, 0.0, 3227.41, 501, 5321, 3998.6000000000004, 4416.999999999997, 4978.880000000002, 27.99552071668533, 12.166022186450169, 21.297373670212767], "isController": false}, {"data": ["Submit answer 2", 300, 0, 0.0, 4293.35666666667, 3026, 5712, 4677.5, 5081.5999999999985, 5362.960000000001, 22.232103156958647, 8.510726989773232, 18.345827312138727], "isController": false}, {"data": ["Submit answer 3", 300, 0, 0.0, 3974.1833333333334, 3085, 5288, 4433.5, 4673.349999999999, 5056.47, 21.258503401360542, 8.138020833333334, 17.56317761479592], "isController": false}, {"data": ["Submit answer 4", 300, 0, 0.0, 4397.296666666668, 3039, 7024, 4845.9, 5526.049999999999, 5669.89, 19.99333555481506, 7.65369876707764, 16.517931522825723], "isController": false}, {"data": ["Submit answer 5", 300, 0, 0.0, 4249.706666666668, 2817, 5389, 4761.500000000001, 4873.7, 5157.830000000001, 19.535065442469232, 7.478267239695254, 16.13932164485251], "isController": false}, {"data": ["Login as teacher", 1, 0, 0.0, 45.0, 45, 45, 45.0, 45.0, 45.0, 22.22222222222222, 27.821180555555557, 2.8862847222222223], "isController": false}, {"data": ["Submit answer 6", 300, 0, 0.0, 4146.00333333333, 2707, 5194, 4394.1, 4808.7, 4959.64, 20.820320632937747, 7.970278992296481, 17.201163335415366], "isController": false}, {"data": ["Submit answer 7", 300, 0, 0.0, 4039.5100000000034, 3212, 5408, 4261.9, 4698.0, 4941.860000000001, 19.28268414963363, 7.3816525260316235, 15.930811318935596], "isController": false}, {"data": ["Submit answer 8", 300, 0, 0.0, 4078.1400000000012, 2797, 5354, 4283.9, 4619.0, 4871.83, 19.48178453146308, 7.457870640950712, 16.095302454704854], "isController": false}, {"data": ["Submit answer 9", 300, 0, 0.0, 3927.283333333336, 3028, 5219, 4217.5, 4357.95, 4674.97, 19.252984212552946, 7.370283018867924, 15.887472323835194], "isController": false}, {"data": ["Submit null answer 4", 300, 0, 0.0, 4647.079999999999, 3275, 6206, 5224.200000000003, 5444.7, 5638.76, 20.029376418747496, 7.667495660301776, 16.508587595139538], "isController": false}, {"data": ["Create quiz", 1, 0, 0.0, 49.0, 49, 49, 49.0, 49.0, 49.0, 20.408163265306122, 247.66820790816325, 31.529017857142858], "isController": false}, {"data": ["Submit answer 1", 300, 0, 0.0, 3943.766666666668, 2927, 6064, 4458.0, 4638.349999999999, 5640.800000000005, 23.22340919647004, 8.890211333023688, 19.186527519739897], "isController": false}, {"data": ["Get list of questions", 1, 0, 0.0, 80.0, 80, 80, 80.0, 80.0, 80.0, 12.5, 1091.29638671875, 9.48486328125], "isController": false}, {"data": ["Start quiz", 300, 0, 0.0, 3643.653333333333, 2248, 5436, 4320.5, 4628.749999999997, 5205.800000000002, 28.749401054144705, 183.5524788871586, 21.786655486344035], "isController": false}, {"data": ["Submit answer 20", 300, 0, 0.0, 3890.5000000000005, 2782, 5714, 4139.400000000001, 4520.5, 5009.9000000000015, 21.343198634035286, 8.170443227091633, 17.653993401394423], "isController": false}, {"data": ["Debug Sampler", 300, 0, 0.0, 0.12333333333333348, 0, 2, 1.0, 1.0, 1.0, 44.06580493537015, 80.10724400521445, 0.0], "isController": false}, {"data": ["Create question", 20, 0, 0.0, 48.95, 43, 55, 53.900000000000006, 54.95, 55.0, 20.202020202020204, 20.026436237373737, 23.79458648989899], "isController": false}, {"data": ["Submit answer 19", 300, 0, 0.0, 3904.9333333333325, 3130, 4878, 4133.6, 4394.7, 4652.7300000000005, 19.686331124089506, 7.536173633440514, 16.283518029398255], "isController": false}, {"data": ["Submit answer 18", 300, 0, 0.0, 4033.1466666666656, 2848, 5449, 4279.5, 4597.45, 4957.8, 19.913707268503153, 7.623216063723864, 16.471591851974775], "isController": false}, {"data": ["Submit answer 17", 300, 0, 0.0, 4002.8766666666647, 3148, 4984, 4337.8, 4517.3, 4763.430000000001, 19.077901430842605, 7.3032591414944354, 15.780256359300477], "isController": false}]}, function(index, item){
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
