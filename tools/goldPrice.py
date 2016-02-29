import urllib2
import re

def goldPriceFunc(): 
	#regular expressions:
	goldPriceRegex = '<div id="gold_spot_3"(.+)<span(.+)>\$([\d\.]+)(.+)span(.+)'

	#get the html
	headers = { 'User-Agent' : 'Mozilla/5.0' }
	url = 'http://www.pmbull.com/gold-price/'
	req = urllib2.Request(url, None, headers)
	htmlFile = urllib2.urlopen(req)
	htmlText = htmlFile.read()
	#print htmlText

	goldPricePattern = re.compile(goldPriceRegex)
	goldPriceInfo = re.findall(goldPricePattern, htmlText)

	return goldPriceInfo


print goldPriceFunc()
