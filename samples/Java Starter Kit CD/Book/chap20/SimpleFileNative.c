#include "SimpleFile.h"     /* for unhand(), among other things */

#include <sys/param.h>      /* for MAXPATHLEN */
#include <fcntl.h>          /* for O_RDWR and O_CREAT */

#define LOCAL_PATH_SEPARATOR  '/'    /* for UNIX */

static void  fixSeparators(char *p) {
	for (; *p != '\0'; ++p)
		if (*p == SimpleFile_separatorChar)
			*p = LOCAL_PATH_SEPARATOR;
}

long  SimpleFile_open(struct HSimpleFile  *this) {
	int   fd;
	char  buffer[MAXPATHLEN];

	javaString2CString(unhand(this)->path, buffer, sizeof(buffer));
	fixSeparators(buffer);
	if ((fd = open(buffer, O_RDWR | O_CREAT, 0664)) < 0)    /* UNIX open */
		return(FALSE); /* or, SignalError() could throw an exception */
	unhand(this)->fd = fd;         /* save fd in the Java world */
	return(TRUE);
}

void  SimpleFile_close(struct HSimpleFile  *this) {
	close(unhand(this)->fd);
	unhand(this)->fd = -1;
}

long  SimpleFile_read(struct HSimpleFile  *this, HArrayOfByte  *buffer,
                                                           long  count) {
	char  *data     = unhand(buffer)->body;  /* get array data   */
	int    length   = obj_length(buffer);    /* get array length */
	int    numBytes = (length < count ? length : count);

	if ((numBytes = read(unhand(this)->fd, data, numBytes)) == 0)
		return(-1);
	return(numBytes);       /* the number of bytes actually read */
}

long  SimpleFile_write(struct HSimpleFile  *this, HArrayOfByte  *buffer,
                                                            long  count) {
	char  *data   = unhand(buffer)->body;
	int    length = obj_length(buffer);

	return(write(unhand(this)->fd, data, (length < count ? length : count)));
}
/* paren must be added */
