#THIS SCRIPTS USES THE VERSION OF WIKIQA DATASET AVAILABLE ON OCT 2 AT https://www.microsoft.com/en-us/download/confirmation.aspx?id=52419

#!/bin/bash

if [ $# != 1 ]
  then
    echo "sh download_and_convert_wikiqa.sh <path_to_the_unpacked_distribution_of_wikiqa>"
  exit
fi

wikiqa_location=${1}
mkdir data/wikiQA

echo "Converting train file"
python scripts/converters/wikiqa_convert.py ${wikiqa_location}/WikiQA-train.tsv data/wikiQA/WikiQA-train.questions.txt  data/wikiQA/WikiQA-train.tsv.resultset

echo "Converting test file"
python scripts/converters/wikiqa_convert.py ${wikiqa_location}/WikiQA-test.tsv data/wikiQA/WikiQA-test.questions.txt  data/wikiQA/WikiQA-test.tsv.resultset

echo "Converting dev file"
python scripts/converters/wikiqa_convert.py ${wikiqa_location}/WikiQA-dev.tsv data/wikiQA/WikiQA-dev.questions.txt  data/wikiQA/WikiQA-dev.tsv.resultset

echo "done, output written to data/wikiQA"