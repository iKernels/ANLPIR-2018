import argparse
import codecs
import random

def get_arg_parser():
  parser = argparse.ArgumentParser(description='Extract subset of train corpus')

  parser.add_argument('-i', '--input_question_file', dest="input_question_file", default=None, required=True)


  parser.add_argument('-o', '--output_question_file', dest="output_question_file", default=None, required=True)

  parser.add_argument('-p', '--percentage_of_questions_to_keep', dest="percentage_of_questions_to_keep",
                      default=0.1, type=float, required=False)

  parser.add_argument('-s', '--seed', default=512, type=int, dest="seed", required=False)
  return parser

if __name__ == '__main__':
    parser = get_arg_parser()

    args = parser.parse_args()
    qids = set()

    random.seed(args.seed)


    questions = dict()
    with codecs.open(args.input_question_file, 'r', encoding='utf8') as inputfile:
        for line in inputfile:
            qid, text = line.strip().split(" ",1)
            questions[qid] = text

    num_to_keep = int(len(questions.keys())*args.percentage_of_questions_to_keep)



    q_to_keep = random.sample(list(questions.items()), num_to_keep)

    print "Keeping %d questions out of %d" % (len(q_to_keep), len(questions.keys()))
    with open(args.output_question_file, "w") as fw:
        for qid, text in q_to_keep:
            fw.write("%s %s\n" % (qid, text))
    print "Wrote partial question file to ", args.output_question_file



