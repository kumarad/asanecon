package com.techan.custom;

import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;

import org.xml.sax.XMLReader;

public class CustomTagHandler implements Html.TagHandler {
    // The following variables track our progress for a list within the html content.
    private boolean bulletOpenTag = true;
    private String listParentTag = null;
    private int bulletIndex = 1;

    @Override
    public void handleTag(boolean opening,
                          String tag,
                          Editable output,
                          XMLReader xmlReader) {
        if(tag.equalsIgnoreCase("strike") || tag.equals("s")) {
            processStrike(opening, output);
        } else if(tag.equals("ul") || tag.equals("ol") || tag.equals("li")) {
            processListTags(tag, output);
        }
    }

    private void processListTags(String tag, Editable output) {
        // If we find that the tag is ul or ol its the start of a list.
        // Mark the fact that we are now handling the list by setting parent.
        // Also append a new line so that there is a line separating the list from
        // the rest of the html.
        if(tag.equals("ul")) {
            listParentTag = "ul";
            output.append("\n");
        } else if(tag.equals("ol")) {
            listParentTag = "ol";
            output.append("\n");
        }

        if(tag.equals("li")){
            // We have encountered an item within a list.
            if(listParentTag.equals("ul")){
                // Its just a bulleted list.
                if(bulletOpenTag) {
                    // This is the open tag.
                    output.append("\n\t• ");
                    bulletOpenTag = false;
                } else {
                    // Close tag. So the next one we see will be for the next bullet.
                    bulletOpenTag = true;
                    output.append("\n");
                }
            } else {
                // Its a numbered list.
                if(bulletOpenTag) {
                    // This is the open tag.
                    output.append(String.format("\n\t%d. ", bulletIndex));
                    bulletOpenTag = false;
                    bulletIndex++;
                } else {
                    // This is the close tag. Next one will be the next bullet open tag.
                    bulletOpenTag = true;
                }
            }
        }
    }

    private void processStrike(boolean opening, Editable output) {
        int curContentIndex = output.length();
        if(opening) {
            // Set a marker for where the strike through starts in the content.
            output.setSpan(new StrikethroughSpan(), curContentIndex, curContentIndex, Spannable.SPAN_MARK_MARK);
        } else {
            // We have hit the closing tag.
            // Find the start marker for the strike through.
            Object spanStartMarkObj = getLast(output, StrikethroughSpan.class);
            if(spanStartMarkObj != null) {
                // Figure out start location of marker in the html content.
                int startIndex = output.getSpanStart(spanStartMarkObj);

                // We are done with this tag. So remove the start marker.
                output.removeSpan(spanStartMarkObj);

                // Set the strike through for the length of the tag scope.
                if(startIndex != curContentIndex) {
                    output.setSpan(new StrikethroughSpan(), startIndex, curContentIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } // else something went wrong. Just move along ..... Should never happen.
        }
    }

    private Object getLast(Editable text, Class kind) {
        // Get the spans of the specific type.
        Object[] spanObjs = text.getSpans(0, text.length(), kind);

        if (spanObjs.length == 0) {
            // Didn't find any spans of the specified kind. Just return a null.
            return null;
        } else {
            // Iterate looking for the recent most marker for the span.
            for(int i = spanObjs.length-1; i >= 0 ; i--) {
                if(text.getSpanFlags(spanObjs[i]) == Spannable.SPAN_MARK_MARK) {
                    // Found the marker.
                    return spanObjs[i];
                }
            }

            // Didn't find a marker for that type ...... Shouldn't happen technically.
            return null;
        }
    }
}