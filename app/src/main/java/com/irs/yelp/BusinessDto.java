package com.irs.yelp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model defining structure of businesses in Yelp JSON response
 */
public class BusinessDto implements Parcelable {
    double rating;
    String price;
    String phone;
    String id;
    boolean is_closed;
    CategoriesDto[] categories;
    int review_count;
    String name;
    String url;
    CoordinatesDto coordinates;
    String coordinate;
    String image_url;
    LocationDto location;
    String address;
    double distance;

    protected BusinessDto(Parcel in) {
        rating = in.readDouble();
        price = in.readString();
        phone = in.readString();
        id = in.readString();
        is_closed = in.readByte() != 0;
        review_count = in.readInt();
        name = in.readString();
        url = in.readString();
        coordinate = in.readString();
        image_url = in.readString();
        address = in.readString();
        distance = in.readDouble();
    }

    public static final Creator<BusinessDto> CREATOR = new Creator<BusinessDto>() {
        @Override
        public BusinessDto createFromParcel(Parcel in) {
            return new BusinessDto(in);
        }

        @Override
        public BusinessDto[] newArray(int size) {
            return new BusinessDto[size];
        }
    };

    public double rating() {
        return rating;
    }

    public String price() {
        return price;
    }

    public String phone() {
        return phone;
    }

    public String id() {
        return id;
    }

    public boolean is_closed() {
        return is_closed;
    }

    public CategoriesDto[] categories() {
        return categories;
    }

    public int review_count() {
        return review_count;
    }

    public String name() {
        return name;
    }

    public String url() {
        return url;
    }

    public CoordinatesDto coordinates() {
        return coordinates;
    }

    public String image_url() {
        return image_url;
    }

    public LocationDto location() {
        return location;
    }

    public double distance() {
        return distance;
    }

    public String toString() {
        return this.name + "\t" + this.phone + "\t" + this.rating + "\t" + this.price + "\t" + this.categories[0] + "\t" + this.location;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(rating);
        dest.writeString(price);
        dest.writeString(phone);
        dest.writeString(id);
        dest.writeByte((byte) (is_closed ? 1 : 0));
        dest.writeInt(review_count);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(coordinates.toString());
        dest.writeString(image_url);
        dest.writeString(location.toString());
        dest.writeDouble(distance);
    }


    /**
     * Convert to BusinessDto array from parcelable array
     *
     * @param parcelables parcelables to convert from
     * @return array of objects
     */
    public static BusinessDto[] toBusinessModel(final Parcelable[] parcelables) {
        BusinessDto[] businesses = new BusinessDto[parcelables.length];
        System.arraycopy(parcelables, 0, businesses, 0, parcelables.length);
        return businesses;
    }
}
