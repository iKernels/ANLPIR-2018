date

echo 'nohup java -XX:ParallelGCThreads=4 -Xmx4G -Xss512m it.unitn.nlpir.system.core.ClassTextPairConversion -questionsPath data/wikiQA/WikiQA-train.questions.toy.txt -answersPath data/wikiQA/WikiQA-train.tsv.resultset -outputDir data/examples/wikiqa_toy_CH_23-05-2018 -filePersistence CASes/wikiqa/train -candidatesToKeep 10 -expClassName it.unitn.nlpir.experiment.fqa.CHExperiment -mode train   > logs/java_train_wikiqa_toy_CH_23-05-2018.log 2>&1 & '
nohup java -XX:ParallelGCThreads=4 -Xmx4G -Xss512m it.unitn.nlpir.system.core.ClassTextPairConversion -questionsPath data/wikiQA/WikiQA-train.questions.toy.txt -answersPath data/wikiQA/WikiQA-train.tsv.resultset -outputDir data/examples/wikiqa_toy_CH_23-05-2018 -filePersistence CASes/wikiqa/train -candidatesToKeep 10 -expClassName it.unitn.nlpir.experiment.fqa.CHExperiment -mode train   > logs/java_train_wikiqa_toy_CH_23-05-2018.log 2>&1 & 
echo 'nohup java -XX:ParallelGCThreads=4 -Xmx4G -Xss512m it.unitn.nlpir.system.core.ClassTextPairConversion -questionsPath data/wikiQA/WikiQA-dev.questions.txt -answersPath data/wikiQA/WikiQA-dev.tsv.resultset -outputDir data/examples/wikiqa_toy_CH_23-05-2018 -filePersistence CASes/wikiqa/dev -candidatesToKeep 1000 -expClassName it.unitn.nlpir.experiment.fqa.CHExperiment -mode test   -skipAllSame > logs/java_dev_wikiqa_toy_CH_23-05-2018.log 2>&1 &'
nohup java -XX:ParallelGCThreads=4 -Xmx4G -Xss512m it.unitn.nlpir.system.core.ClassTextPairConversion -questionsPath data/wikiQA/WikiQA-dev.questions.txt -answersPath data/wikiQA/WikiQA-dev.tsv.resultset -outputDir data/examples/wikiqa_toy_CH_23-05-2018 -filePersistence CASes/wikiqa/dev -candidatesToKeep 1000 -expClassName it.unitn.nlpir.experiment.fqa.CHExperiment -mode test   -skipAllSame > logs/java_dev_wikiqa_toy_CH_23-05-2018.log 2>&1 &
echo 'nohup java -XX:ParallelGCThreads=4 -Xmx4G -Xss512m it.unitn.nlpir.system.core.ClassTextPairConversion -questionsPath data/wikiQA/WikiQA-test.questions.txt -answersPath data/wikiQA/WikiQA-test.tsv.resultset -outputDir data/examples/wikiqa_toy_TEST_CH_23-05-2018 -filePersistence CASes/wikiqa/test -candidatesToKeep 1000 -expClassName it.unitn.nlpir.experiment.fqa.CHExperiment -mode test   -skipAllSame > logs/java_test_wikiqa_toy_CH_23-05-2018.log 2>&1 &'
nohup java -XX:ParallelGCThreads=4 -Xmx4G -Xss512m it.unitn.nlpir.system.core.ClassTextPairConversion -questionsPath data/wikiQA/WikiQA-test.questions.txt -answersPath data/wikiQA/WikiQA-test.tsv.resultset -outputDir data/examples/wikiqa_toy_TEST_CH_23-05-2018 -filePersistence CASes/wikiqa/test -candidatesToKeep 1000 -expClassName it.unitn.nlpir.experiment.fqa.CHExperiment -mode test   -skipAllSame > logs/java_test_wikiqa_toy_CH_23-05-2018.log 2>&1 &

wait

date


echo 'nohup tools/SVM-Light-1.5-rer/svm_learn -t 5 -F 3 -C T -m 1000 data/examples/wikiqa_toy_CH_23-05-2018/svm.train data/examples/wikiqa_toy_CH_23-05-2018/svm_T.model > logs/svm_train_wikiqa_toy_CH_23-05-2018_T.log 2>&1 &'
nohup tools/SVM-Light-1.5-rer/svm_learn -t 5 -F 3 -C T -m 1000 data/examples/wikiqa_toy_CH_23-05-2018/svm.train data/examples/wikiqa_toy_CH_23-05-2018/svm_T.model > logs/svm_train_wikiqa_toy_CH_23-05-2018_T.log 2>&1 &

wait

date


export OMP_NUM_THREADS=4
export OMP_STACKSIZE=1048576
echo 'nohup tools/SVM-Light-1.5-rer/svm_classify data/examples/wikiqa_toy_CH_23-05-2018/svm.test data/examples/wikiqa_toy_CH_23-05-2018/svm_T.model data/examples/wikiqa_toy_CH_23-05-2018/svm_T.pred > logs/svm_dev_wikiqa_toy_CH_23-05-2018_T.log 2>&1 &'
nohup tools/SVM-Light-1.5-rer/svm_classify data/examples/wikiqa_toy_CH_23-05-2018/svm.test data/examples/wikiqa_toy_CH_23-05-2018/svm_T.model data/examples/wikiqa_toy_CH_23-05-2018/svm_T.pred > logs/svm_dev_wikiqa_toy_CH_23-05-2018_T.log 2>&1 &
echo 'nohup tools/SVM-Light-1.5-rer/svm_classify data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm.test data/examples/wikiqa_toy_CH_23-05-2018/svm_T.model data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm_T.pred > logs/svm_test_wikiqa_toy_CH_23-05-2018_T.log 2>&1 &'
nohup tools/SVM-Light-1.5-rer/svm_classify data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm.test data/examples/wikiqa_toy_CH_23-05-2018/svm_T.model data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm_T.pred > logs/svm_test_wikiqa_toy_CH_23-05-2018_T.log 2>&1 &

wait

date


echo 'python scripts/eval/ev.py --ignore_noanswer --ignore_allanswer data/examples/wikiqa_toy_CH_23-05-2018/svm.relevancy data/examples/wikiqa_toy_CH_23-05-2018/svm_T.pred'
python scripts/eval/ev.py --ignore_noanswer --ignore_allanswer data/examples/wikiqa_toy_CH_23-05-2018/svm.relevancy data/examples/wikiqa_toy_CH_23-05-2018/svm_T.pred
echo 'python scripts/eval/ev.py --ignore_noanswer --ignore_allanswer data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm.relevancy data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm_T.pred'
python scripts/eval/ev.py --ignore_noanswer --ignore_allanswer data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm.relevancy data/examples/wikiqa_toy_TEST_CH_23-05-2018/svm_T.pred
date

