/* text areas */

import java.awt.*;

public class TextAreaTest extends java.applet.Applet {

  public void init() {
    String str = "Once upon a midnight dreary, while I pondered, weak and weary,\n" +
"Over many a quaint and curious volume of forgotten lore,\n" +
"While I nodded, nearly napping, suddenly there came a tapping,\n" +
"As of some one gently rapping, rapping at my chamber door.\n" +
"\"'Tis some visitor,\" I muttered, \"tapping at my chamber door-\n" +
"Only this, and nothing more.\"\n\n" +
"Ah, distinctly I remember it was in the bleak December,\n" +
"And each separate dying ember wrought its ghost upon the floor.\n" +
"Eagerly I wished the morrow;- vainly I had sought to borrow\n" +
"From my books surcease of sorrow- sorrow for the lost Lenore-\n" +
"For the rare and radiant maiden whom the angels name Lenore-\n" +
"Nameless here for evermore.";

     add(new TextArea(str,10,60));
  }
  public boolean action(Event evt, Object arg) {
    System.out.println("Action");
    return true;
  }

}
