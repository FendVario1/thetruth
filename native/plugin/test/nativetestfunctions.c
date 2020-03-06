#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include <jni.h>
#include "../java/eu_rationality_thetruth_Weechat.h"

#define UNUSED(X) (void) X;

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_trigger_1pending_1operations
  (JNIEnv *env, jclass class)
{
	UNUSED(env);
	UNUSED(class);

}

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_print
  (JNIEnv *env, jclass class, jlong bufferid, jstring jstr)
{
	UNUSED(class);
}

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_print_1prefix
  (JNIEnv *env, jclass class, jlong bufferid, jstring jprefix, jstring jstr)
{
	UNUSED(class);
}

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_print_1date_1tags
  (JNIEnv *env, jclass class, jlong bufferid, jlong date, jstring jtags, jstring jmessage)
{
	UNUSED(class);
}

JNIEXPORT jlong JNICALL Java_eu_rationality_thetruth_Weechat_buffer_1new
  (JNIEnv *env, jclass class, jstring jname)
{
	UNUSED(class);
}

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_buffer_1set
  (JNIEnv *env, jclass class, jlong bufferid, jstring jproperty, jstring jval)
{
	UNUSED(class);
}


JNIEXPORT jlong JNICALL Java_eu_rationality_thetruth_Weechat_nicklist_1add_1nick
  (JNIEnv *env, jclass class, jlong bufferid, jstring jnick, jstring jcolor, jstring jprefix)
{
	UNUSED(class);
	return 0;
}

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_nicklist_1remove_1nick
  (JNIEnv *env, jclass class, jlong bufferid, jlong nickid)
{
	UNUSED(env);
	UNUSED(class);
}

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_nicklist_1remove_1all
  (JNIEnv *env, jclass class, jlong bufferid)
{
	UNUSED(env);
	UNUSED(class);
}

JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_nicklist_1nick_1set
  (JNIEnv *env, jclass class, jlong bufferid, jlong nickid, jstring jproperty, jstring jval)
{
	UNUSED(class);
}

/*JNIEXPORT void JNICALL Java_eu_rationality_thetruth_Weechat_1initUser
  (JNIEnv *env, jclass class, jstring juser, jstring jpassword)
{

}*/
