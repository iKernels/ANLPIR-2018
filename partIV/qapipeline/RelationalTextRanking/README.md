The detailed documentation on how to set up all possible experimental configurations will be made avaialble on March 10, 2017. An [end-to-end example](#running-an-end-to-end-example) usage is already available.

**RelTextRank** is flexible Java pipeline for converting pairs of raw texts into structured representations and enriching them with semantic information about the relations between the two pieces of text (e.g., lexical exact match). 


# Installation

## Prerequisites
The tool requires the following prerequisites:
*	Java 1.8+
*	Apache Maven > 3.3.9. Refer to http://maven.apache.org/install.html for the installation instructions

## Installing
```bash
git clone https://github.com/iKernels/ANLPIR-2018.git
```
if you have not cloned the course repo yet, otherwise do simply ```bash git pull```.

```bash
cd ./partIV/qapipeline/RelationalTextRanking
```
cd ./RelationalTextRanking

export JAVA_HOME=<path to your JDK distribution>

sh scripts/install/install.sh 

rm target/dependency/google-collections-1.0.jar 

export CLASSPATH=${CLASSPATH}:bin/:target/dependency/*:target/classes

```

# Running the experiments

## Downloading the WikiQA dataset
In this lab we will run experiments on the WikiQA dataset.
First you need to download the WikiQA data from https://www.microsoft.com/en-us/download/details.aspx?id=52419. 
Then run the following commands from the root of the RelTextRank distribution.

```bash
export wikiqa_location=<folder to which you unpacked the WikiQa distribution>
mkdir data/wikiQA
python scripts/converters/wikiqa_convert.py ${wikiqa_location}/WikiQA-train.tsv data/wikiQA/WikiQA-train.questions.txt  data/wikiQA/WikiQA-train.tsv.resultset
python scripts/converters/wikiqa_convert.py ${wikiqa_location}/WikiQA-test.tsv data/wikiQA/WikiQA-test.questions.txt  data/wikiQA/WikiQA-test.tsv.resultset
python scripts/converters/wikiqa_convert.py ${wikiqa_location}/WikiQA-dev.tsv data/wikiQA/WikiQA-dev.questions.txt  data/wikiQA/WikiQA-dev.tsv.resultset
```

It may take a long time to train the pipeline on the full-scale data on  your personal machine. Therefore, try training on a subset of the training set.
Run the following command to prepare the input file with the toy input data:
```bash
python scripts/converters/extract_trainset_subset.py -i data/wikiQA/WikiQA-train.questions.txt -o  data/wikiQA/WikiQA-train.questions.toy.txt -p 0.3
```

## Running experiments
To train on full-scale data (will take time):
```bash
export corpus_name=wikiqa
```
OR 

To train on toy data (hopefully, fast):
```bash
export corpus_name=wikiqa_toy
```

You may run experiments with the following structures as written below. The experiment launcher script generates a script file with the commands inside.

### Shallow chunk-based structure
```bash
python scripts/experiment_launchers/experiment_launcher.py  -l ${corpus_name} -o scripts/generated_scripts -c CH -p "-t 5 -F 3 -C T -m 1000"  -e it.unitn.nlpir.experiment.fqa.CHExperiment -suf T -s it.unitn.nlpir.system.core.ClassTextPairConversion -ate " -skipAllSame" -ade " -skipAllSame"
```
### CONST structure
```bash
python scripts/experiment_launchers/experiment_launcher.py  -l ${corpus_name} -o scripts/generated_scripts -c CONST -p "-t 5 -F 3 -C T -m 1000"  -e it.unitn.nlpir.experiment.fqa.ConstExperiment -suf T -s it.unitn.nlpir.system.core.ClassTextPairConversion -ate " -skipAllSame" -ade " -skipAllSame"
```
### DT1 structure
```bash
python scripts/experiment_launchers/experiment_launcher.py  -l ${corpus_name} -o scripts/generated_scripts -c DT1 -p "-t 5 -F 3 -C T -m 1000"  -e it.unitn.nlpir.experiment.fqa.DT1Experiment -suf T -s it.unitn.nlpir.system.core.ClassTextPairConversion  -ate " -skipAllSame" -ade " -skipAllSame"
```

### DT2 structure
```bash
#DT2 structure
python scripts/experiment_launchers/experiment_launcher.py  -l ${corpus_name} -o scripts/generated_scripts -c DT2 -p "-t 5 -F 3 -C T -m 1000"  -e it.unitn.nlpir.experiment.fqa.DT2Experiment -suf T -s it.unitn.nlpir.system.core.ClassTextPairConversion  -ate " -skipAllSame" -ade " -skipAllSame"
```

### DT3q_DT2a structure
```bash
python scripts/experiment_launchers/experiment_launcher.py  -l ${corpus_name} -o scripts/generated_scripts -c DT3q_DT2a -p "-t 5 -F 3 -C T -m 1000"  -e it.unitn.nlpir.experiment.fqa.LCTqDT2aExperiment -suf T -s it.unitn.nlpir.system.core.ClassTextPairConversion  -ate " -skipAllSame" -ade " -skipAllSame"
```

