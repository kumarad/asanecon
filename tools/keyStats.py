import urllib
import re

def keyStatFunc(symbol): #enter a stock symbol
	#regular expressions:
	marketCapRegex = '<span id="yfs_j10_' + symbol + '">(.+?)</span>'
	keyStatLabelRegex = '<td class="yfnc_tablehead1".+?>(.+?)<.+?</td>'
	keyStatRegex = '<td class="yfnc_tabledata1">(.+?)</td>'

	#get the html
	url = 'http://finance.yahoo.com/q/ks?s=' + symbol + '+Key+Statistics'
	htmlFile = urllib.urlopen(url)
	htmlText = htmlFile.read()
	#print htmlText

	# Get the labels.
	keyStatLabelPattern = re.compile(keyStatLabelRegex)
	keyStatsLabels = re.findall(keyStatLabelPattern, htmlText)

	# Get the values for the labels
	keyStatPattern = re.compile(keyStatRegex)
	keyStats = re.findall(keyStatPattern, htmlText)

	# Make a dict out of the labels and values.
	stats = dict(zip(keyStatsLabels, keyStats))

	# Return them.
	return stats


print keyStatFunc("ibm")
