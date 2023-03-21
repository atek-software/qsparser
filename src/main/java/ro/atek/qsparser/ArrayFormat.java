package ro.atek.qsparser;

/**
 * This is used when generating a query string from the internal representation.
 * Based on the format, the arrays will be encoded in special ways.
 */
public enum ArrayFormat
{
   /** The array keys should always end up with empty square brackets */
   BRACKETS,
   /** The array elements should be delimited by comma on a single key*/
   COMMA,
   /** The array keys should always end up with square brackets and indices*/
   INDICES,
   /** Simply repeat the key. The parser will combine the values into an array automatically. */
   REPEAT
}
