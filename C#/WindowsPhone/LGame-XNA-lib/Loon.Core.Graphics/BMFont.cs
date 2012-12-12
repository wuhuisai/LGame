using System;
using System.IO;
using System.Collections.Generic;
using Microsoft.Xna.Framework;
using Loon.Utils.Collection;
using Loon.Core.Graphics.Opengl;
using Loon.Core.Resource;
using Loon.Utils;
using Loon.Java;

namespace Loon.Core.Graphics
{
    public class BMFont
    {

        private const int DEFAULT_MAX_CHAR = 255;

        private ArrayMap displays;

        private int lazyHashCode = 1;

        private LTexture displayList;

        private CharDef[] chars;

        private int lineHeight;

        private bool isClose;

        private string info, common, page;

        private class Display
        {

            internal string text;

            internal Loon.Core.Graphics.Opengl.LTextureBatch.GLCache cache;

            internal int width;

            internal int height;
        }

        private class CharDef
        {

            internal short id;

            internal short tx;

            internal short ty;

            internal short width;

            internal short height;

            internal short xoffset;

            internal short yoffset;

            internal short advance;

            internal short[] kerning;

            BMFont _font;

            public CharDef(BMFont font)
            {
                _font = font;
            }

            public void Draw(float x, float y)
            {
                if (_font.isClose)
                {
                    return;
                }
                _font.displayList.Draw(x + xoffset, y + yoffset, width, height, tx, ty,
                        tx + width, ty + height);
            }

            public int GetKerning(int point)
            {
                if (kerning == null)
                {
                    return 0;
                }
                int low = 0;
                int high = kerning.Length - 1;
                while (low <= high)
                {
                    int midIndex = (int)((uint)(low + high) >> 1);
                    int value = kerning[midIndex];
                    int foundCodePoint = value & 0xff;
                    if (foundCodePoint < point)
                    {
                        low = midIndex + 1;
                    }
                    else if (foundCodePoint > point)
                    {
                        high = midIndex - 1;
                    }
                    else
                    {
                        return value >> 8;
                    }
                }
                return 0;
            }
        }

        public BMFont(string file, LTexture image)
        {
            this.displayList = image;
            this.Parse(Resources.OpenStream(file));
        }

        public BMFont(string file, string imgFile)
        {
            this.displayList = new LTexture(imgFile);
            this.Parse(Resources.OpenStream(file));
        }

        private void Parse(InputStream file)
        {
            if (displays == null)
            {
                displays = new ArrayMap(DEFAULT_MAX_CHAR);
            }
            else
            {
                displays.Clear();
            }
            try
            {
                StreamReader ins = new StreamReader(file,
                        System.Text.Encoding.UTF8);
                info = ins.ReadLine();
                common = ins.ReadLine();
                page = ins.ReadLine();

                ArrayMap kerning = new ArrayMap(
                        64);
                List<CharDef> charDefs = new List<CharDef>(
                        DEFAULT_MAX_CHAR);

                int maxChar = 0;
                bool done = false;
                for (; !done; )
                {
                    string line = ins.ReadLine();
                    if (line == null)
                    {
                        done = true;
                    }
                    else
                    {
                        if (line.StartsWith("chars c"))
                        {
                        }
                        else if (line.StartsWith("char"))
                        {
                            CharDef def = ParseChar(line);
                            if (def != null)
                            {
                                maxChar = MathUtils.Max(maxChar, def.id);
                                charDefs.Add(def);
                            }
                        }
                        if (line.StartsWith("kernings c"))
                        {
                        }
                        else if (line.StartsWith("kerning"))
                        {
                            StringTokenizer tokens = new StringTokenizer(line, " =");
                            tokens.NextToken();
                            tokens.NextToken();
                            short first = short.Parse(tokens.NextToken());
                            tokens.NextToken();
                            int second = int.Parse(tokens.NextToken());
                            tokens.NextToken();
                            int offset = int.Parse(tokens.NextToken());
                            List<short> values = (List<short>)kerning.GetValue(first);
                            if (values == null)
                            {
                                values = new List<short>();
                                kerning.Put(first, values);
                            }
                            values.Add((short)((offset << 8) | second));
                        }
                    }
                }

                this.chars = new CharDef[maxChar + 1];

                for (IEnumerator<CharDef> iter = charDefs.GetEnumerator(); iter.MoveNext(); )
                {
                    CharDef def = (CharDef)iter.Current;
                    chars[def.id] = def;
                }
                ArrayMap.Entry[] entrys = kerning.ToEntrys();
                for (int j = 0; j < entrys.Length; j++)
                {
                    ArrayMap.Entry entry = entrys[j];
                    short first = (short)entry.GetKey();
                    List<short> valueList = (List<short>)entry.GetValue();
                    short[] valueArray = new short[valueList.Count];
                    int i = 0;
                    for (IEnumerator<short> valueIter = valueList.GetEnumerator(); valueIter
                            .MoveNext(); i++)
                    {
                        valueArray[i] = (short)valueIter.Current;
                    }
                    chars[first].kerning = valueArray;
                }
            }
            catch (IOException)
            {
                throw new Exception("Invalid font file: " + file);
            }
        }


        private CharDef ParseChar(string line)
        {
            CharDef def = new CharDef(this);
            StringTokenizer tokens = new StringTokenizer(line, " =");

            tokens.NextToken();
            tokens.NextToken();
            def.id = short.Parse(tokens.NextToken());

            if (def.id < 0)
            {
                return null;
            }
            if (def.id > DEFAULT_MAX_CHAR)
            {
                throw new Exception(def.id + " > " + DEFAULT_MAX_CHAR);
            }

            tokens.NextToken();
            def.tx = short.Parse(tokens.NextToken());
            tokens.NextToken();
            def.ty = short.Parse(tokens.NextToken());
            tokens.NextToken();
            def.width = short.Parse(tokens.NextToken());
            tokens.NextToken();
            def.height = short.Parse(tokens.NextToken());
            tokens.NextToken();
            def.xoffset = short.Parse(tokens.NextToken());
            tokens.NextToken();
            def.yoffset = short.Parse(tokens.NextToken());
            tokens.NextToken();
            def.advance = short.Parse(tokens.NextToken());

            if (def.id != ' ')
            {
                lineHeight = MathUtils.Max(def.height + def.yoffset, lineHeight);
            }

            return def;
        }

        public void DrawString(float x, float y, string text)
        {
            DrawString(x, y, text, null);
        }

        public void DrawString(float x, float y, string text, LColor col)
        {
            DrawBatchString(x, y, text, col, 0, text.Length - 1);
        }

        private void DrawBatchString(float tx, float ty, string text, LColor c,
                int startIndex, int endIndex)
        {

            if (isClose)
            {
                return;
            }

            if (displays.Size() > DEFAULT_MAX_CHAR)
            {
                displays.Clear();
            }

            lazyHashCode = 1;

            if (c != null)
            {
                lazyHashCode = LSystem.Unite(lazyHashCode, c.r);
                lazyHashCode = LSystem.Unite(lazyHashCode, c.g);
                lazyHashCode = LSystem.Unite(lazyHashCode, c.b);
                lazyHashCode = LSystem.Unite(lazyHashCode, c.a);
            }

            string key = text + lazyHashCode;

            Display display = (Display)displays.Get(key);

            if (display == null)
            {

                int x = 0, y = 0;

                displayList.GLBegin();
                displayList.SetBatchPos(tx, ty);

                if (c != null)
                {
                    displayList.SetImageColor(c);
                }

                CharDef lastCharDef = null;
                char[] data = text.ToCharArray();
                for (int i = 0; i < data.Length; i++)
                {
                    int id = data[i];
                    if (id == '\n')
                    {
                        x = 0;
                        y += GetLineHeight();
                        continue;
                    }
                    if (id >= chars.Length)
                    {
                        continue;
                    }
                    CharDef charDef = chars[id];
                    if (charDef == null)
                    {
                        continue;
                    }

                    if (lastCharDef != null)
                    {
                        x += lastCharDef.GetKerning(id);
                    }
                    lastCharDef = charDef;

                    if ((i >= startIndex) && (i <= endIndex))
                    {
                        charDef.Draw(x, y);
                    }

                    x += charDef.advance;
                }

                if (c != null)
                {
                    displayList.SetImageColor(LColor.white);
                }

                displayList.GLEnd();

                display = new Display();

                display.cache = displayList.NewBatchCache();
                display.text = text;
                display.width = 0;
                display.height = 0;

                displays.Put(key, display);

            }
            else if (display.cache != null)
            {
                display.cache.x = tx;
                display.cache.y = ty;
                LTextureBatch.Commit(displayList, display.cache);
            }

        }

        public int GetHeight(string text)
        {
            if (text == null)
            {
                return 0;
            }
            int lines = 0;
            int height = 0;
            for (int i = 0; i < text.Length; i++)
            {
                int id = text[i];
                if (id == '\n')
                {
                    lines++;
                    height = 0;
                    continue;
                }
                if (id == ' ')
                {
                    continue;
                }
                CharDef charDef = chars[id];
                if (charDef == null)
                {
                    continue;
                }
                height = MathUtils.Max(charDef.height + charDef.yoffset,
                        height);
            }
            height += lines * GetLineHeight();
            return height;
        }

        public int GetWidth(string text)
        {
            if (text == null)
            {
                return 0;
            }
            int width = 0;
            CharDef lastCharDef = null;
            for (int i = 0, n = text.Length; i < n; i++)
            {
                int id = text[i];
                if (id == '\n')
                {
                    width = 0;
                    continue;
                }
                if (id >= chars.Length)
                {
                    continue;
                }
                CharDef charDef = chars[id];
                if (charDef == null)
                {
                    continue;
                }
                if (lastCharDef != null)
                {
                    width += lastCharDef.GetKerning(id);
                }
                lastCharDef = charDef;
                if (i < n - 1)
                {
                    width += charDef.advance;
                }
                else
                {
                    width += charDef.width;
                }
                width = MathUtils.Max(charDef.width, width);
            }

            return width;
        }

        public string GetCommon()
        {
            return common;
        }

        public string GetInfo()
        {
            return info;
        }

        public string GetPage()
        {
            return page;
        }

        public int GetLineHeight()
        {
            return lineHeight;
        }

        public void Dispose()
        {
            this.isClose = true;
            if (displayList != null)
            {
                displayList.Destroy();
                displayList = null;
            }
            if (displays != null)
            {
                for (int i = 0; i < displays.Size(); i++)
                {
                    Display d = (Display)displays.Get(i);
                    if (d != null && d.cache != null)
                    {
                        d.cache.Dispose();
                        d.cache = null;
                    }
                }
                displays.Clear();
                displays = null;
            }
        }
    }
}
