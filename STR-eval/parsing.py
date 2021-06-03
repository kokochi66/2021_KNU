import os
import json

dir = './tag/'
file_list=os.listdir(dir)

log = open(f'./tag.txt', 'w', encoding='utf8')

for file in file_list:
	with open('./tag/' + file, 'r') as f:
		json_data = json.load(f)
		
	tags=json_data.get("regions")

	data = []
	s=""

	for argm in tags:
		s+=argm['tags'][0]

	log.write(file + " : " + s.lower())
	log.write('\n')
