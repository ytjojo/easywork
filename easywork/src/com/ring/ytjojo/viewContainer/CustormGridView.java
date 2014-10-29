package com.ring.ytjojo.viewContainer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.GridView;

/**
 * listView�߶�����Ӧ���
 * <һ�仰���ܼ���>
 * <������ϸ����>
 *
 * @author lijing
 * @version [�汾��, 2013-8-24]
 * @see [�����/����]
 * @since [��Ʒ/ģ��汾]
 */
public class CustormGridView extends GridView
{

    public CustormGridView (Context context, AttributeSet attrs, int defStyle)
    {
        super (context, attrs, defStyle);
    }

    public CustormGridView (Context context, AttributeSet attrs)
    {
        super (context, attrs);
    }

    public CustormGridView (Context context)
    {
        super (context);
    }

    @Override
    public boolean dispatchKeyEvent (KeyEvent event)
    {
        return false;
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        //���ģʽ����ÿ��child�ĸ߶ȺͿ��
        int expandSpec = MeasureSpec.makeMeasureSpec (Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure (widthMeasureSpec, expandSpec);
    }
}
