package wolve.dms.libraries;

import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import java.util.ArrayList;

import wolve.dms.models.BaseModel;
import wolve.dms.utils.Constants;
import wolve.dms.utils.Util;

public class Contacts {
    public static void InsertContact(BaseModel customer){
        if (!Util.isPhoneFormat(customer.getString("phone")).equals("")){
            if (Contacts.contactExists(customer.getString("phone"))){
                Util.showToast("Da ton tai");
            }else {
                String name = String.format("%s %s. %s",
                        Constants.shopNameShortened[customer.getInt("shopType")],
                        customer.getString("signBoard"),
                        customer.getString("name").substring(customer.getString("name").lastIndexOf(" ")+1));

                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                        .withValue(RawContacts.ACCOUNT_TYPE, AccountManager.KEY_ACCOUNT_TYPE)
                        .withValue(RawContacts.ACCOUNT_NAME, AccountManager.KEY_ACCOUNT_NAME)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                        //.withValue(ContactsContract.CommonDataKinds.StructuredName.IN_VISIBLE_GROUP,true)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, customer.getString("phone"))
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, "")
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, "")
                        .build());

                try {
                    Util.getInstance().getCurrentActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (
                        RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (
                        OperationApplicationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    public static boolean contactExists(String number) {
        ContentResolver cr = Util.getInstance().getCurrentActivity().getContentResolver();
        Cursor curContacts = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (curContacts.moveToNext()) {
            String contactNumber = curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (number.equals(contactNumber)) {
                return true;
            }
        }
        return false;


    }

}