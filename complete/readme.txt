Proposed utility is meant to calculate simple moving average (SMA) of all listed companies which come under NSE Nifty 50 index (company wise csv files are dumped to an input location)
and a report is generated of top 'n' companies having daily closing price lower than calculated simple moving average.
Simple Moving Average will be based on past 2 years of data.

This utility is designed as a Spring boot application and a POST request 'http://localhost:8080/submitJob' will initiate the process i.e.
an autosys job will be scheduled to trigger this POST request on dai;y basis.

1. Will read all the files from the input directory (path specified in the application.properties)
2. Divide all the csvs into batches (provided as threshold)
3. In each batch calculate the SMA for each company and validate if its is greater than the current day's closing price.
4. If the above condition is satisfied, the company date is added to the current batche's result list.
5. Each batch is then sorted based on the SMA, and top 'n' results of each batch are only considered.
6. Fork Join mechanism is used to process all the batches parallely and efficiently.
7. Finally the result lists of all the batches are merged, sorted and trimmed down for top 'n' companies data.
8. The final result is written to a file in the specified output directory.
