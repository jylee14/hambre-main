package com.irs.yelp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model representing Yelp response to a business search request
 */
public class BusinessResponseDto implements Parcelable {
    private String total;
    private BusinessDto[] businesses;
    private RegionDto region;

    protected BusinessResponseDto(Parcel in) {
        total = in.readString();
        businesses = in.createTypedArray(BusinessDto.CREATOR);
    }

    public static final Creator<BusinessResponseDto> CREATOR = new Creator<BusinessResponseDto>() {
        @Override
        public BusinessResponseDto createFromParcel(Parcel in) {
            return new BusinessResponseDto(in);
        }

        @Override
        public BusinessResponseDto[] newArray(int size) {
            return new BusinessResponseDto[size];
        }
    };

    public String total() {
        return total;
    }

    public BusinessDto[] businesses() {
        return businesses;
    }

    public RegionDto region() {
        return region;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeTypedArray(businesses, 0);
        dest.writeString(total);
        dest.writeTypedArray(businesses, flags);
    }
}

