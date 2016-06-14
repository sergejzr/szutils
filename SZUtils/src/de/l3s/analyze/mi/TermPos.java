package de.l3s.analyze.mi;



/**
* Objekte dieser Klasse sind Paare aus Termen und ihrer Position
* innerhalb eines Dokuments  
*/
public class TermPos implements Comparable<TermPos>
{ 
  private String term ; 
  private int pos ; 

  public TermPos( String aTerm , int aPos )
  {
    term = aTerm ; 
    pos = aPos ; 
  }

  public String getTerm()
  {
    return term ; 
  }

  public int getPos()
  {
    return pos ; 
  }

  public String toString()
  {
    return "(" + term + "," + pos + ")\n" ; 
  }

  public int compareTo(TermPos other)
  {
    TermPos otherTermPos = (TermPos)other; 
    String otherTerm = otherTermPos.getTerm(); 
    return term.compareTo(otherTerm); 
  }


} 



  
