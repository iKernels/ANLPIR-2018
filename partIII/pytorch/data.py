import os

def load(category):
    reviews = []
    for f in os.listdir('txt_sentoken/'+category):    
        with open('txt_sentoken/'+category+'/'+f) as inp:
            reviews.append(inp.read().strip('\n'))
    return reviews
