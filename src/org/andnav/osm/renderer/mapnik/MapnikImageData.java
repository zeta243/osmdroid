package org.andnav.osm.renderer.mapnik;

// Original from include/mapnik/image_data.hpp
// Template class, for either 8-bit or 32-bit images. We dont need 8-bit, so drop the template
// and use int[] instead.

// I suspect that this class can be replaced by a bitmap/canvas, however I want to get all
// the code ported before making any implementation changes.

public class MapnikImageData {

	final int mWidth;
	final int mHeight;
	int[]         mData;

	public MapnikImageData(int width, int height)
	{
		mWidth  = width;
		mHeight = height;
		
		if (width != 0 && height != 0)
		{
			
		}
		mData = new int[width * height];
		// memset()...
		for (int i = 0; i < mData.length; i++)
		{
			mData[i] = 0;
		}
	}
	
	
/*(
        ImageData(const ImageData<T>& rhs)
            :width_(rhs.width_),
             height_(rhs.height_),
             pData_((rhs.width_!=0 && rhs.height_!=0)?
                    static_cast<T*>(::operator new(sizeof(T)*rhs.width_*rhs.height_)) :0)
        {
            if (pData_) memcpy(pData_,rhs.pData_,sizeof(T)*rhs.width_* rhs.height_);
        }
        inline T& operator() (unsigned i,unsigned j)
        {
            assert(i<width_ && j<height_);
            return pData_[j*width_+i];
        }
        inline const T& operator() (unsigned i,unsigned j) const
        {
            assert(i<width_ && j<height_);
            return pData_[j*width_+i];
        }
        inline unsigned width() const
        {
            return width_;
        }
        inline unsigned height() const
        {
            return height_;
        }
        inline void set(const T& t)
        {
            for (unsigned y = 0; y < height_; ++y)
            {
               T * row = getRow(y);
               for (unsigned x = 0; x < width_; ++x)
               {
                  row[x] = t;
               }
            }
        }

        inline const T* getData() const
        {
            return pData_;
        }
        inline T* getData()
        {
            return pData_;
        }

        inline const unsigned char* getBytes() const
        {
            return (unsigned char*)pData_;
        }

        inline unsigned char* getBytes()
        {
            return (unsigned char*)pData_;
        }

        inline const T* getRow(unsigned row) const
        {
            return pData_+row*width_;
        }

        inline T* getRow(unsigned row)
        {
            return pData_+row*width_;
        }

        inline void setRow(unsigned row,const T* buf,unsigned size)
        {
            assert(row<height_);
            assert(size<=(width_*sizeof(T)));
            memcpy(pData_+row*width_,buf,size*sizeof(T));
        }
        inline void setRow(unsigned row,unsigned x0,unsigned x1,const T* buf)
        {
            memcpy(pData_+row*width_+x0,buf,(x1-x0)*sizeof(T));
        }

        inline ~ImageData()
        {
            ::operator delete(pData_),pData_=0;
        }

    private:

          ImageData& operator=(const ImageData&);
    };

   typedef ImageData<unsigned> ImageData32;
   typedef ImageData<uint8_t>  ImageData8;

 */
	
}
